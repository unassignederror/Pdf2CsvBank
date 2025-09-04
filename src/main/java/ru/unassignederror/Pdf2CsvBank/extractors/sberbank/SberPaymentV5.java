package ru.unassignederror.Pdf2CsvBank.extractors.sberbank;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.unassignederror.Pdf2CsvBank.Extractor;
import ru.unassignederror.Pdf2CsvBank.Operation;

public class SberPaymentV5 implements Extractor {
	private static final Pattern OPERATION_PATTERN = Pattern.compile(
			"(\\d{2}.\\d{2}.\\d{4})\\s+\\d{2}:\\d{2}\\s+(\\d{6})\\s+(.+?)\\s+([+-]?[\\d\\s\\u00A0]+,\\d{2})\\s+([\\d\\s\\u00A0]+,\\d{2})\\n(?:\\d{2}.\\d{2}.\\d{4}\\s+)(.*(?:\\n(?!\\d{2}.\\d{2}.\\d{4}\\s).*)*)");

	private static final String[] UNWANTED_PATTERNS = {
			"Продолжение на следующей странице",
			"Выписка по платёжному счёту Страница \\d+ из \\d+",
			"ДАТА ОПЕРАЦИИ \\(МСК\\)", "Дата обработки¹ и код авторизации",
			"КАТЕГОРИЯ", "Описание операции", "СУММА В ВАЛЮТЕ СЧЁТА",
			"Сумма в валюте", " операции²", "ОСТАТОК СРЕДСТВ",
			" В ВАЛЮТЕ СЧЁТА"};

	@Override
	public boolean isSupported(String text) {
		return text.contains("ПАО Сбербанк")
				&& text.contains("Выписка по платёжному счёту")
				&& text.contains("Управляющий директор Дивизиона");
	}

	@Override
	public List<Operation> parse(String text) {
		System.out.println("Формат выписки: SberPaymentV5");

		List<Operation> operations = new ArrayList<>();
		String cleanedText = cleanText(text);

		Matcher matcher = OPERATION_PATTERN.matcher(cleanedText);

		while (matcher.find()) {
			Operation operation = new Operation();
			operation.setDate(matcher.group(1));
			operation.setAuthCode(matcher.group(2));
			operation.setCategory(matcher.group(3));
			operation.setAmount(formatAmount(matcher.group(4)));
			operation.setBalance(matcher.group(5));
			operation.setDescription(
					matcher.group(6).replaceAll("\\s+", " ").trim());

			operations.add(operation);
		}

		return operations;
	}

	private String cleanText(String text) {
		// Удаляем верхний блок (от начала до таблицы операций)
		text = removeTopSection(text);

		// Удаляем нижний блок (от "Дата формирования" до конца)
		text = removeBottomSection(text);

		for (String pattern : UNWANTED_PATTERNS) {
			text = text.replaceAll(pattern, "");
		}

		return text;
	}

	private static String removeTopSection(String text) {
		// Маркер начала таблицы операций
		String operationsStartMarker = "Расшифровка операций";
		int startIndex = text.indexOf(operationsStartMarker);

		if (startIndex != -1) {
			// Находим начало данных операций (после заголовков)
			String firstOperationPattern = "\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}";
			Pattern pattern = Pattern.compile(firstOperationPattern);
			Matcher matcher = pattern.matcher(text.substring(startIndex));

			if (matcher.find()) {
				int operationStart = startIndex + matcher.start();
				return text.substring(operationStart);
			}
		}

		return text;
	}

	private static String removeBottomSection(String text) {
		// Маркер начала нижнего блока
		String bottomStartMarker = "Дергунова К. А.";
		int startIndex = text.indexOf(bottomStartMarker);

		if (startIndex != -1) {
			return text.substring(0, startIndex).trim();
		}

		return text;
	}

	private String formatAmount(String amount) {
		if (amount == null || amount.isEmpty())
			return "-";
		return amount.charAt(0) != '+' ? "-" + amount : amount;
	}

}
