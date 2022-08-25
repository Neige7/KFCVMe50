package pers.neige.kfcvme50

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import pers.neige.kfcvme50.manager.ConfigManager.config
import taboolib.common.platform.Plugin
import taboolib.platform.BukkitPlugin
import java.util.*
import java.util.logging.Level

object KFCVMe50 : Plugin() {
    val logger by lazy { Bukkit.getServer().logger }

    val plugin by lazy { BukkitPlugin.getInstance() }

    private lateinit var task: BukkitTask

    override fun onEnable() {
        task = loadTask()
    }

    private fun loadTask(): BukkitTask {
        return object : BukkitRunnable() {
            override fun run() {
                val date = Date()
                // 如果当前是星期四
                if (date.day == 4) {
                    // 伟大的KFC之神进行提示
                    logger.log(Level.SEVERE, config.getString("messages.default", "java.lang.KFCException: KFC Fucking Crazy Thursday!!"))
                }
            }
        }.runTaskTimerAsynchronously(plugin, config.getLong("period", 20L), config.getLong("period", 20L))
    }

    fun reload() {
        task.cancel()
        task = loadTask()
    }
}