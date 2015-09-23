package net.headlezz.resdiff

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options

public class CLOptions(args: Array<String>) {

    private val options = Options()
    private val clOptions: CommandLine

    init {
        options.addOption("h", "help", false, "Show this screen")
        options.addOption("t", "type", true, "only compare this resource type (string, bool, integer, color, dimen)")
        options.addOption("y", "side-by-side", false, "output in columns")

        val optionParser = DefaultParser()
        clOptions = optionParser.parse(options, args)
    }

    public fun printHelp() {
        HelpFormatter().printHelp("resdiff [OPTIONS]... FILES", options)
    }

    public fun hasFlag(flag: String) : Boolean = clOptions.hasOption(flag)

    public fun getOptionValue(flag: String) : String = clOptions.getOptionValue(flag)

}