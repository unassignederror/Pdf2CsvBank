package ru.unassignederror.Pdf2CsvBank;

import java.util.List;

public interface Extractor {
	boolean isSupported(String text);
	List<Operation> parse(String text);
}
