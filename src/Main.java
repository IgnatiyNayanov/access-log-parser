import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
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
/*Задание #1. Обработка исключений */
            int totalLines = 0;
            int maxLength = 0;
            int minLength = 1024;
            try (FileReader fileReader = new FileReader(path);
                 BufferedReader reader = new BufferedReader(fileReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalLines++;
                    int length = line.length();
                    if (length > 1024) {
                        throw new LineTooLongException("Ошибка. Строка № " + totalLines + " превышает максимальную допустимую длину 1024 символов");
                    }

                    if (length > maxLength) {
                        maxLength = length;
                    }

                    if (length < minLength) {
                        minLength = length;
                    }
                }

                System.out.println("Общее количество строк в файле: " + totalLines);
                System.out.println("Длина самой длинной строки: " + maxLength);
                System.out.println("Длина самой короткой строки: " + minLength);

            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            /*Задание #1. Обработка исключений*/
        }
    }
}