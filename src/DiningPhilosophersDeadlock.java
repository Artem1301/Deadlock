import java.util.concurrent.Semaphore;

class Philosopher extends Thread {
    private final int id;
    private final Semaphore leftChopstick, rightChopstick;

    public Philosopher(int id, Semaphore leftChopstick, Semaphore rightChopstick) {
        this.id = id;
        this.leftChopstick = leftChopstick;
        this.rightChopstick = rightChopstick;
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking.");
        Thread.sleep((int) (Math.random() * 1000));
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + id + " is eating.");
        Thread.sleep((int) (Math.random() * 1000));
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();
                leftChopstick.acquire();
                System.out.println("Philosopher " + id + " picked up left chopstick.");

                rightChopstick.acquire();
                System.out.println("Philosopher " + id + " picked up right chopstick.");

                eat();

                rightChopstick.release();
                leftChopstick.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/* Способи уникнення deadlock.
 *
 * 1. Зміна порядку взяття паличок:
 *    Одним із простих способів уникнення deadlock є зміна порядку взяття паличок для деяких філософів.
 *    Наприклад, філософи з парними ID можуть спочатку брати ліву паличку, а потім праву,
 *    тоді як філософи з непарними ID — праву паличку, а потім ліву.
 *    Це розриває циклічну залежність, яка може спричинити взаємне блокування.
 *    Це просте і ефективне рішення для невеликої кількості ресурсів.
 *
 * 2. Метод ієрархії ресурсів:
 *    Кожному ресурсу (паличці) присвоюється унікальний номер. Філософи завжди беруть палички
 *    в порядку зростання номерів: спочатку паличку з меншим номером, а потім з більшим.
 *    Цей метод запобігає циклічному очікуванню, оскільки ніколи не виникає ситуація, коли
 *    філософ чекає на паличку з меншим номером після того, як він взяв паличку з більшим номером.
 *    Недолік методу — він вимагає попереднього визначення ресурсів у системі та їх впорядкування.
 *
 * 3. Використання таймаутів (timeout):
 *    Кожен філософ, намагаючись взяти палички, може встановити обмеження по часу (таймаут)
 *    на очікування ресурсів. Якщо він не може отримати обидві палички протягом певного часу,
 *    він звільняє ресурси, які вже отримав, і повертається до роздумів (think).
 *    Це дозволяє уникнути довготривалого блокування, але може призвести до більшої кількості
 *    «голодних» філософів, які часто відмовляються від паличок.
 *
 * 4. Алгоритм Бенкера (Banker’s Algorithm):
 *    Цей алгоритм використовується для уникнення deadlock шляхом перевірки, чи залишиться система
 *    в безпечному стані після надання ресурсу. Алгоритм перевіряє, чи існує можливий порядок завершення
 *    процесів (у даному випадку — філософів), який дозволить уникнути deadlock.
 *    Перед тим як дозволити філософу взяти палички, система симулює можливі результати і визначає,
 *    чи можуть усі філософи завершити їжу без взаємного блокування.
 *    Це дуже ефективний метод, але він складний у реалізації через велику кількість обчислень.
 *
 */


public class DiningPhilosophersDeadlock {
    public static void main(String[] args) {
        int numPhilosophers = 5;
        Semaphore[] chopsticks = new Semaphore[numPhilosophers];

        for (int i = 0; i < numPhilosophers; i++) {
            chopsticks[i] = new Semaphore(1);
        }

        Philosopher[] philosophers = new Philosopher[numPhilosophers];
        for (int i = 0; i < numPhilosophers; i++) {
            philosophers[i] = new Philosopher(i, chopsticks[i], chopsticks[(i + 1) % numPhilosophers]);
            philosophers[i].start();
        }
    }
}
