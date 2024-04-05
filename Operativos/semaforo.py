import multiprocessing
import time

class Semaphore:
    def __init__(self, initial):
        self.lock = multiprocessing.Lock()
        self.value = multiprocessing.Value('i', initial)

    def acquire(self):
        with self.lock:
            if self.value.value > 0:
                self.value.value -= 1
                return True
            else:
                return False

    def release(self):
        with self.lock:
            self.value.value += 1

def worker(semaphore, id, max_count):
    print(f'Worker {id} is waiting to enter')
    while not semaphore.acquire():
        time.sleep(0.1)  # Polling the semaphore
    print(f'Worker {id} is in the critical section')
    local_counter = 0
    for i in range(max_count):
        local_counter += 1
        semaphore.release()
        time.sleep(0.1)
        semaphore.acquire()
    print(f'Worker {id} is leaving the critical section')
    return local_counter

if __name__ == "__main__":
    semaphore = Semaphore(1)  # Initial value of semaphore
    max_count = 10  # Maximum count for each worker
    processes = []

    expected_result = max_count * 5  # Expected result for all workers
    actual_result = 0

    for i in range(5):  # Create 5 worker processes
        p = multiprocessing.Process(target=worker, args=(semaphore, i, max_count))
        processes.append(p)
        p.start()

    for p in processes:
        actual_result += p.join()

    print("All processes finished.")
    print(f'Expected result: {expected_result}')
    print(f'Actual result: {actual_result}')
