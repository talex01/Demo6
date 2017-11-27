/*
---------
Задание 4
---------

Напишите класс, реализующий интерфейс Runnable, содержащий поле-счетчик. Метод run() должен наращивать
этот счетчик. Создайте пять экземпляров этого класса и запустите их в пяти потоках с разными
приоритетами. После 1 секунды работы остановите потоки и сравните значения счетчиков.
*/

package org.project06_4;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class App {
    static long start;
    private static final CyclicBarrier BARRIER = new CyclicBarrier(5, () -> {
        System.out.println("\nBarrier is open\n");
        start = System.nanoTime();
    });

    public static void main(String[] args) throws InterruptedException {

        for (int i = 1; i <= 5; i++) {
            new Thread(new PriorityClass(i * 2)).start();
            Thread.sleep(500);
        }
    }

    private static class PriorityClass implements Runnable {
        private int count = 0;
        int priority;

        PriorityClass(int priority) {
            this.priority = priority;
        }

        @Override
        public void run() {
            try {
                Thread.currentThread().setPriority(this.priority);
                System.out.println(Thread.currentThread().getName() + " priority " + Thread.currentThread().getPriority() + " is ready...");
                BARRIER.await();
                System.out.println(Thread.currentThread().getName() + " started at\t" + System.nanoTime() + "\nbarrier opened at\t" + start);
                while (System.nanoTime() - start < 1_000_000_000) {
                    count++;
                }
                System.out.println(Thread.currentThread().getName() + " priority = " + Thread.currentThread().getPriority() + " : count = " + count);
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
/*
ИТОГИ:
Результаты сильно отличаются в зависимости от операционной системы и конфигурации компьютера.
На хорошем компьютере цифры соизмеримы, на более старом наблюдается значительное уменьшение
значения счетчика при более низком приоритете потока. Выше приоритет - больше значение.
Тестирование производилось на следующих машинах:
Intel Xeon E5430 @ 2.66 GHz(4 ядра) Windows 10 x64
Intel G3240 @ 3.10GHz (2 ядра) Linux Mint 18.1 x64
Intel G3240 @ 3.10GHz (2 ядра) Windows 7 sp1 x64
*/