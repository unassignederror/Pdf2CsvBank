package ru.unassignederror.Pdf2CsvBank;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import ru.unassignederror.Pdf2CsvBank.extractors.sberbank.SberPaymentV5;
import ru.unassignederror.Pdf2CsvBank.extractors.sberbank.SberPaymentV6;

public class Pdf2CsvBank {
	private final List<Extractor> extractors;

	public Pdf2CsvBank() {
		extractors = new ArrayList<>();
		extractors.add(new SberPaymentV6());
		extractors.add(new SberPaymentV5());
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println(
					"Использование: java Pdf2CsvBank <входной файл> [выходной файл]");
			return;
		}

		String inputFile = args[0];
		String outputFile = args.length > 1
				? args[1]
				: inputFile.replaceFirst("\\.[^.]+$", "") + ".csv";

		try {
			Pdf2CsvBank parser = new Pdf2CsvBank();
			parser.convert(inputFile, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeToCsv(List<Operation> operations, String filename)
			throws IOException {
		try (FileWriter writer = new FileWriter(filename)) {
			writer.append(
					"Дата,Код авторизации,Категория,Описание,Сумма,Остаток\n");

			for (Operation op : operations) {
				writer.append(String.format(
						"\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
						op.getDate(), op.getAuthCode(), op.getCategory(),
						op.getDescription().replace("\"", "\"\""),
						op.getAmount(), op.getBalance()));
			}
		}
	}

	public void convert(String inputFilePath, String outputFilePath)
			throws IOException {
		String text = extractTextFromFile(new File(inputFilePath));
		Extractor extractor = findSuitableExtractor(text);

		if (extractor == null) {
			throw new IllegalArgumentException(
					"Не удалось определить формат выписки");
		}

		writeToCsv(extractor.parse(text), outputFilePath);
	}

	private String extractTextFromFile(File file) throws IOException {
		if (!file.getName().toLowerCase().endsWith(".pdf")) {
			throw new IllegalArgumentException(
					"Неподдерживаемый формат файла: " + file.getName());
		}

		try (PDDocument document = Loader
				.loadPDF(new RandomAccessReadBufferedFile(file))) {
			return new PDFTextStripper().getText(document);
		}
	}

	private Extractor findSuitableExtractor(String text) {
		return extractors.stream()
				.filter(extractor -> extractor.isSupported(text)).findFirst()
				.orElse(null);
	}

}
