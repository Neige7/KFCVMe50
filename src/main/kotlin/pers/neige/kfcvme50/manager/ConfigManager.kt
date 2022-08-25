package pers.neige.kfcvme50.manager

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import pers.neige.kfcvme50.KFCVMe50.plugin
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.module.metrics.Metrics
import java.io.InputStreamReader

// 配置文件管理器, 用于管理config.yml文件, 对其中缺少的配置项进行主动补全, 同时释放默认配置文件
object ConfigManager {
    /**
     * 获取默认Config
     */
    private val originConfig: FileConfiguration =
        plugin.getResource("config.yml")?.let { YamlConfiguration.loadConfiguration(InputStreamReader(it, "UTF-8")) } ?: YamlConfiguration()

    /**
     * 获取配置文件
     */
    val config get() = plugin.config

    /**
     * 保存默认配置文件
     */
    @Awake(LifeCycle.INIT)
    fun saveResource() {
        // 保存默认配置文件
        plugin.saveDefaultConfig()
        // 加载bstats
        Metrics(16270, plugin.description.version, Platform.BUKKIT)
    }

    /**
     * 对当前Config查缺补漏
     */
    @Awake(LifeCycle.LOAD)
    fun loadConfig() {
        originConfig.getKeys(true).forEach { key ->
            if (!plugin.config.contains(key)) {
                plugin.config.set(key, originConfig.get(key))
            } else {
                val completeValue = originConfig.get(key)
                val value = plugin.config.get(key)
                if (completeValue is ConfigurationSection && value !is ConfigurationSection) {
                    plugin.config.set(key, completeValue)
                } else {
                    plugin.config.set(key, value)
                }
            }
        }
        plugin.saveConfig()
    }

    /**
     * 重载配置管理器
     */
    fun reload() {
        plugin.reloadConfig()
        loadConfig()
    }
}