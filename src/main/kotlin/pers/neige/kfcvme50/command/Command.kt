package pers.neige.kfcvme50.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pers.neige.kfcvme50.KFCVMe50
import pers.neige.kfcvme50.manager.ConfigManager
import pers.neige.kfcvme50.manager.ConfigManager.config
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.submit
import taboolib.module.chat.TellrawJson
import taboolib.platform.BukkitAdapter
import java.util.logging.Level
import kotlin.math.ceil

@CommandHeader(name = "kfc")
object Command {
    private val bukkitAdapter = BukkitAdapter()

    @CommandBody
    val main = mainCommand {
        execute<CommandSender> { sender, _, _ ->
            submit(async = true) {
                help(sender)
            }
        }
        incorrectCommand { sender, _, _, _ ->
            help(sender)
        }
    }

    @CommandBody
    val error = subCommand {
        dynamic(commit = "message") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                arrayListOf("报错内容")
            }
            execute<CommandSender> { _, _, argument ->
                KFCVMe50.logger.log(Level.SEVERE, argument)
            }
            dynamic(optional = true, commit = "amount") {
                suggestion<CommandSender>(uncheck = true) { _, _ ->
                    arrayListOf("重复次数")
                }
                execute<CommandSender> { _, context, argument ->
                    submit(async = true) {
                        repeat(argument.toIntOrNull() ?: 1) {
                            KFCVMe50.logger.log(Level.SEVERE, context.argument(-1))
                        }
                    }
                }
            }
        }
    }

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            reloadCommand(sender)
        }
    }

    private fun reloadCommand(sender: CommandSender) {
        submit(async = true) {
            ConfigManager.reload()
            KFCVMe50.reload()
            sender.sendMessage(config.getString("messages.reloadedMessage"))
        }
    }

    private fun help(
        sender: CommandSender,
        page: Int = 1
    ) {
        // 获取帮助信息中的指令信息部分
        config.getConfigurationSection("help.commands")?.let { commandsSection ->
            // 获取所有指令
            val commands = commandsSection.getKeys(false).toMutableList()
            // 获取每一页展示几条指令
            val amount = config.getInt("help.amount")
            // 获取总页数
            val pageAmount = ceil(commands.size.toDouble()/amount.toDouble()).toInt()
            // 确定当前需要打开的页数
            val realPage = page.coerceAtMost(pageAmount).coerceAtLeast(1)
            // 发送前缀
            config.getString("help.prefix")?.let { sender.sendMessage(it) }
            // 获取指令帮助格式
            val format = config.getString("help.format") ?: ""
            // 获取当前序号
            val prevCommandAmount = ((realPage-1)*amount)
            // 遍历指令并发送
            for (index in prevCommandAmount..(prevCommandAmount + amount)) {
                if (index == commands.size) break
                val command = commands[index]
                // 替换信息内变量并发送
                sender.sendMessage(format
                    .replace("{command}", commandsSection.getString("$command.command") ?: "")
                    .replace("{description}", commandsSection.getString("$command.description") ?: ""))
            }
            val prevRaw = TellrawJson()
                .append(config.getString("help.prev")?:"")
            if (realPage != 1) {
                prevRaw
                    .hoverText((config.getString("help.prev")?:"") + ": " + (realPage-1).toString())
                    .runCommand("/ni help ${realPage-1}")
            }
            val nextRaw = TellrawJson()
                .append(config.getString("help.next")?:"")
            if (realPage != pageAmount) {
                nextRaw.hoverText((config.getString("help.next")?:"") + ": " + (realPage+1))
                nextRaw.runCommand("/ni help ${realPage+1}")
            }
            var listSuffixMessage = (config.getString("help.suffix")?:"")
                .replace("{current}", realPage.toString())
                .replace("{total}", pageAmount.toString())
            val listMessage = TellrawJson()
            if (sender is Player) {
                listSuffixMessage = listSuffixMessage
                    .replace("{prev}", "!@#$%{prev}!@#$%")
                    .replace("{next}", "!@#$%{next}!@#$%")
                val listSuffixMessageList = listSuffixMessage.split("!@#$%")
                listSuffixMessageList.forEach { value ->
                    when (value) {
                        "{prev}" -> listMessage.append(prevRaw)
                        "{next}" -> listMessage.append(nextRaw)
                        else -> listMessage.append(value)
                    }
                }
                // 向玩家发送信息
                listMessage.sendTo(bukkitAdapter.adaptCommandSender(sender))
            } else {
                sender.sendMessage(listSuffixMessage
                    .replace("{prev}", config.getString("ItemList.Prev")?:"")
                    .replace("{next}", config.getString("ItemList.Next")?:""))
            }
        }
    }

    val commandsPages by lazy { config.getConfigurationSection("help.commands")?.getKeys(false)?.size ?: 1 }

    private fun help(
        sender: ProxyCommandSender,
        page: Int = 1
    ) {
        // 获取帮助信息中的指令信息部分
        config.getConfigurationSection("help.commands")?.let { commandsSection ->
            // 获取所有指令
            val commands = commandsSection.getKeys(false).toMutableList()
            // 获取每一页展示几条指令
            val amount = config.getInt("help.amount")
            // 获取总页数
            val pageAmount = ceil(commands.size.toDouble()/amount.toDouble()).toInt()
            // 确定当前需要打开的页数
            val realPage = page.coerceAtMost(pageAmount).coerceAtLeast(1)
            // 发送前缀
            config.getString("help.prefix")?.let { sender.sendMessage(it) }
            // 获取指令帮助格式
            val format = config.getString("help.format") ?: ""
            // 获取当前序号
            val prevCommandAmount = ((realPage-1)*amount)
            // 遍历指令并发送
            for (index in prevCommandAmount..(prevCommandAmount + amount)) {
                if (index == commands.size) break
                val command = commands[index]
                // 替换信息内变量并发送
                sender.sendMessage(format
                    .replace("{command}", commandsSection.getString("$command.command") ?: "")
                    .replace("{description}", commandsSection.getString("$command.description") ?: ""))
            }
            val prevRaw = TellrawJson()
                .append(config.getString("help.prev")?:"")
            if (realPage != 1) {
                prevRaw
                    .hoverText((config.getString("help.prev")?:"") + ": " + (realPage-1).toString())
                    .runCommand("/ni help ${realPage-1}")
            }
            val nextRaw = TellrawJson()
                .append(config.getString("help.next")?:"")
            if (realPage != pageAmount) {
                nextRaw.hoverText((config.getString("help.next")?:"") + ": " + (realPage+1))
                nextRaw.runCommand("/ni help ${realPage+1}")
            }
            var listSuffixMessage = (config.getString("help.suffix")?:"")
                .replace("{current}", realPage.toString())
                .replace("{total}", pageAmount.toString())
            val listMessage = TellrawJson()
            if (sender is Player) {
                listSuffixMessage = listSuffixMessage
                    .replace("{prev}", "!@#$%{prev}!@#$%")
                    .replace("{next}", "!@#$%{next}!@#$%")
                val listSuffixMessageList = listSuffixMessage.split("!@#$%")
                listSuffixMessageList.forEach { value ->
                    when (value) {
                        "{prev}" -> listMessage.append(prevRaw)
                        "{next}" -> listMessage.append(nextRaw)
                        else -> listMessage.append(value)
                    }
                }
                // 向玩家发送信息
                listMessage.sendTo(bukkitAdapter.adaptCommandSender(sender))
            } else {
                sender.sendMessage(listSuffixMessage
                    .replace("{prev}", config.getString("ItemList.Prev")?:"")
                    .replace("{next}", config.getString("ItemList.Next")?:""))
            }
        }
    }
}