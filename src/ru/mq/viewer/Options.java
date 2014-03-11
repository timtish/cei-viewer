package ru.mq.viewer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.PosixParser;

public class Options extends org.apache.commons.cli.Options {

	private static final long serialVersionUID = -7319273936586839368L;

	public void addParameter(String shortName, String longName, String description) {
		addOption(OptionBuilder.hasArg()
				.withLongOpt(longName)
				.withDescription(description)
				.create(shortName));
	}

	public void addFlag(String shortName, String longName, String description) {
		addOption(OptionBuilder
				.withLongOpt(longName)
				.withDescription(description)
				.create(shortName));
	}

	public CommandLine parse(String[] args) {
		try {
			CommandLineParser parser = new PosixParser();
			return parser.parse(this, args, true);
		} catch (Exception e) {
			return null;
		}
	}

	public void printHelp(String cmdLineSyntax) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(cmdLineSyntax, this);
	}
}
