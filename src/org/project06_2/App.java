/*
---------
Задание 2
---------
Создать программу для загрузки файлов из сети интернет.
Реализовать работу в многопоточном режиме (одновременная загрузка до 5 файлов)
По окончании загрузки вывести информацию о загрузке (где был сохранен файл, его имя, размер файла)
Для загрузки файлов использовать класс URL
*/

package org.project06_2;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Semaphore;

public class App {
    private static final Semaphore SEMAPHORE = new Semaphore(5, true);  //семафор на 5 потоков

    public static void main(String[] args) throws IOException {
        String[] sourceFiles = {    // массив со ссылками на загржаемые файлы
                "http://cdndl.zaycev.net/846434/4153770/Desiigner_-_Panda.mp3",
                "http://cdndl.zaycev.net/89471/1603581/Scorpions_-_Still+Loving+You+.mp3",
                "http://cdndl.zaycev.net/76134/3135835/Nirvana_-_Rape+Me.mp3",
                "http://cdndl.zaycev.net/76243/2054503/No+Doubt_-_Dont+speak+.mp3",
                "http://cdndl.zaycev.net/95611/3157384/Sting_-_Shape+of+my+heart.mp3",
                "http://cdndl.zaycev.net/95611/2575/Sting_-_Fragile.mp3",
                "http://cdndl.zaycev.net/89471/809090/Scorpions_-_Maybe+I+Maybe+You.mp3",
                "http://cdndl.zaycev.net/76134/1590830/Nirvana_-_Heart-shaped+box.mp3",
                "http://cdndl.zaycev.net/89471/7884/Scorpions_-_Send+Me+An+Angel.mp3"
        };

        for (String s : sourceFiles) {  // запуск скачивания всех файлов
            FileDownload fileDownload = new FileDownload(new URL(s));   // создание экземпляров класса, наследующего Thread
            fileDownload.start();
        }
    }

    public static class FileDownload extends Thread {
        private URL url;
        private String destination = "download/";   // куда складывать загруженные файлы
        long startTime;
        long fileSize;

        FileDownload(URL url) {
            this.url = url;
            this.destination += url.getFile().substring(url.getFile().lastIndexOf('/') + 1, url.getFile().length());
        }

        @Override
        public void run() {
            URL u = null;   // урл скачанного файла
            try {
                SEMAPHORE.acquire();    // запуск потока
                System.out.println(Thread.currentThread().getName() + " started");
                startTime = System.currentTimeMillis();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();    // создание соединения с удаленным файлом
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream in = connection.getInputStream();   // открытие потока
                fileSize = connection.getContentLength();   // определение размера загружаемого файла

                OutputStream writer = new FileOutputStream(destination);
                byte buffer[] = new byte[1024];
                int c = in.read(buffer);

                while (c > 0) {
                    writer.write(buffer, 0, c);
                    c = in.read(buffer);
                }
                writer.flush();
                writer.close();
                in.close();
                SEMAPHORE.release();    // файл загружен, освобождение потока
            } catch (IOException e) {
                System.out.println(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("\n" + Thread.currentThread().getName() + ": File downloaded in " + ((System.currentTimeMillis() - startTime) / 1000) + " s");
                try {
                    u = new URL("file://" + destination);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                assert u != null;
                System.out.println("Path: " + u.getHost() + u.getPath() + "\nName: " + u.getFile() + "\nLenght: " + fileSize + " bytes");
            }
        }
    }
}
