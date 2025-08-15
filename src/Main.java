import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {

    /*Задание #2. Сравнение объектов*/
    private static int totalRequests = 0;
    private static int yandexBotCount = 0;
    private static int googleBotCount = 0;
    /*Задание #2. Сравнение объектов*/

    public static void main(String[] args) {
        int count = 0;
        while (true) {
            String path = new Scanner(System.in).nextLine();
            File file = new File(path);
            boolean isFileExists = file.exists();
            boolean isDirectory = file.isDirectory();
            if (!isFileExists || isDirectory) {
                System.out.println("Файл не существует или указан путь к папке");
                count++;
                continue;
            } else {
                System.out.println("Путь указан верно");
            }
            count++;
            System.out.println("Это файл номер " + count);

            /*Задание #2. Сравнение объектов*/
            totalRequests = 0;
            yandexBotCount = 0;
            googleBotCount = 0;
            /*Задание #2. Сравнение объектов*/


            /*Задание #1. Обработка исключений */
//            int maxLength = 0;
//            int minLength = 1024;
            try (FileReader fileReader = new FileReader(path);
                 BufferedReader reader = new BufferedReader(fileReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalRequests++;
                    int length = line.length();
                    if (length > 1024) {
                        throw new LineTooLongException("Ошибка. Строка № " + totalRequests + " превышает максимальную допустимую длину 1024 символов");
                    }


                    /*Задание #2. Сравнение объектов*/
                    String[] parts = split(line);
                    if (parts.length >= 9) {
                        analyzeUserAgent(parts[8]);
                    }
                    /*Задание #2. Сравнение объектов*/

//                    if (length > maxLength) {
//                        maxLength = length;
//                    }
//
//                    if (length < minLength) {
//                        minLength = length;
//                    }
                }


                System.out.println("Общее количество строк в файле: " + totalRequests);
                printBotStatistics();
                // код не удалил, а закомментировал, чтобы можно было проверить предыдущее задание
//                System.out.println("Длина самой длинной строки: " + maxLength);
//                System.out.println("Длина самой короткой строки: " + minLength);

            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            /*Задание #1. Обработка исключений*/
        }


    }

    /*Задание #2. Сравнение объектов*/
    private static String[] split(String line) {
        ArrayList<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean inBrackets = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == '[') {
                inBrackets = true;
            } else if (c == ']') {
                inBrackets = false;
            } else if (c == ' ' && !inQuotes && !inBrackets) {
                if (current.length() > 0) {
                    parts.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            parts.add(current.toString());
        }

        return parts.toArray(new String[parts.size()]);
    }

    private static void analyzeUserAgent(String userAgent) {
        if (userAgent == null || userAgent.equals("-") || userAgent.isEmpty()) {
            return;
        }
        int lastOpenBracket = userAgent.lastIndexOf('(');
        int lastCloseBracket = userAgent.lastIndexOf(')');
        if (lastOpenBracket == -1 || lastCloseBracket == -1) {
            checkForBot(userAgent);
            return;
        }
        String bracketContent = userAgent.substring(lastOpenBracket + 1, lastCloseBracket);
        String[] parts = bracketContent.split(";");
        for (String part : parts) {
            checkForBot(part.trim());
        }

    }

    private static void checkForBot(String fragment) {
        String[] parts = fragment.split("[/\\s]");
        if (parts.length == 0) return;
        String program = parts[0].trim();
        if (program.equalsIgnoreCase("YandexBot")) {
            yandexBotCount++;
        }
        if (program.equalsIgnoreCase("GoogleBot")) {
            googleBotCount++;
        }
    }

    private static void printBotStatistics() {
        double yandexBotPercentage = 100.0 * yandexBotCount / totalRequests;
        double googleBotPercentage = 100.0 * googleBotCount / totalRequests;
        System.out.printf("Доля YandexBot: %.2f%% (%d из %d запросов)%n", yandexBotPercentage, yandexBotCount, totalRequests);
        System.out.printf("Доля GoogleBot: %.2f%% (%d из %d запросов)%n", googleBotPercentage, googleBotCount, totalRequests);
    }
}