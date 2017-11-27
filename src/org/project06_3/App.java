/*
---------
Задание 3
---------

Напишите класс, реализующий интерфейс Runnable, метод run() которого считывает из файла на жестком
диске и выводит в указанный поток какие-либо данные.
Для вывода каждой "порции" данных должно использоваться несколько операций вывода. Операции вывода
должны быть разделены вызовами sleep(100).
Запустите 10 экземпляров этого класса в разных потоках так, чтобы они выводили данные в один и тот
же поток вывода. Вывод информации должен быть синхронизирован так, чтобы в результирующем выходном
потоке порции данных не "перемешивались". Осуществите запись данных из общего потока в отдельный файл
на жестком диске.
*/

package org.project06_3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static java.lang.Thread.sleep;

public class App {
    static final Object lock = new Object();    // lock-объект для синхронизации потоков
    private static final String FILENAME = "alice.txt";

    public static void main(String[] args) throws IOException, InterruptedException {

        for (int i = 0; i < 10; i++) {  // создаем и запускаем 10 потоков
            new Thread(new ReadFile(FILENAME, "cp1251")).start();
        }
    }

    static class ReadFile implements Runnable {
        private RandomAccessFile inputFile;
        private RandomAccessFile outputFile;
        private int len = 10;                   // каждый поток читает по 10 символов
        private int num = 3;                    // 3 раза
        private String charset;                 // кодировка файла
        private static int currentPosition = 0; // текущая позиция чтения файла

        ReadFile(String filename, String charset) throws FileNotFoundException {
            this.inputFile = new RandomAccessFile(filename, "r");    // очень удобный класс для произвольного чтения/записи
            this.outputFile = new RandomAccessFile("output.txt", "rw");
            this.charset = charset;
        }

        void ReadPerPart() throws IOException, InterruptedException {
            byte[] bytes = new byte[len];               // делаем буффер чтения
            while (inputFile.read() != -1) {            // читаем, пока не достигнем конца документа
                for (int i = 0; i < num; i++) {         // каждый поток несколько раз читает кусочек
                    inputFile.seek(currentPosition);    // находим нужную нам позицию для чтения
                    inputFile.read(bytes);              // и читаем кусочек
//                    System.out.print(Thread.currentThread().getName() + ": ");    // раскомментировать для проверки того, что работают разные потоки с файлом
                    System.out.print(new String(bytes, charset));   // выводим прочитанный кусочек на экран
                    outputFile.seek(currentPosition);   // запись в файл по той же технологии
                    outputFile.write(bytes);
                    try {
                        sleep(100);               // задержка для красивого вывода на экран кусочками
                        currentPosition += len;         // меняем текущую позицию курсора
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lock.wait(100);                 // освобождаем монитор
                lock.notifyAll();                       // кричим всем потокам, что можно работать
            }
            inputFile.close();                          // закрываем файл после прочтения
            outputFile.close();                         // закрываем файл после записи
        }

        @Override
        public void run() {
            synchronized (lock) {       // данный блок синхронизируется
                try {
                    ReadPerPart();      // синхронизируемый метод
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}