from concurrent.futures import ThreadPoolExecutor
from threading import Lock, Event
from typing import Callable, Iterable, TypeVar

from text2vql.util.threadsafeQueue import Queue,ShutDown

T = TypeVar('T')  # Type of items in the source queue
R = TypeVar('R')  # Type of items in the output queue

class ConcurrentPipelineProcessor:
    def __init__(self, function: Callable[[T], R], source: Queue[T], workers: int = 16, executor: ThreadPoolExecutor = None) -> None:
        self._flag = Event() # Signal workers that they can process data from input
        self._flag.set()
        self._active_lock = Lock()# Count active workers. Last worker shuts down the output queue.
        self._active = workers

        self.function: Callable[[T], R] = function
        if executor is None:
            self.executor: ThreadPoolExecutor = ThreadPoolExecutor(max_workers=workers)
        else:
            self.executor = executor
        self.source = source
        self.sink: Queue[R] = Queue(workers)

        self.workers = [self.executor.submit(self.wrapper) for _ in range(workers)]


    def wrapper(self):
        try:
            while self._flag.is_set():
                item = self.source.get()
                result = self.function(item)
                if result is not None:
                    self.sink.put(result)
        except Exception as e:
            #print(f"Worker error: {e!r}")   # print the error cause
            pass
        finally:
            self._worker_terminate()

    
    def output(self) -> Queue[R]:
        return self.sink

    def _worker_terminate(self):
        try:
            with self._active_lock:
                self._flag.clear()
                self._active -= 1
                if self._active == 0:
                    self.sink.shutdown()
                    #self.terminate()
                #print(f"Worker shutdown. Remaining {self._active}")
        except Exception as e:
            #print(f"Shutdown error: {e!r}")   # print the error cause
            pass
        
    def terminate(self):
        #print("Terminate pool")
        self._flag.clear()
        self.executor.shutdown()
        #print(f"Pool treminated")

    def join(self):
        self.executor.shutdown()

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_value, traceback):
        self.terminate()





class Iterable2Queue:
    def __init__(self, iterable):
        self.iterable = iterable
        self.iterator = iter(iterable)
        self._lock = Lock()
    def get(self):
        try:
            with self._lock:
                item = next(self.iterator)
                #print(f"Produce {item}")
                return item
        except Exception as e:
            #print(f"Produce: {e!r}")   # print the error cause
            raise e

class Queue2Iterable:
    def __init__(self, queue: Queue):
        self.queue = queue

    def __iter__(self):
        return self

    def __next__(self):
        try:
            item = self.queue.get()
            #print(f"Consume {item}")
            return item
        except Exception as e:
            #print(f"Consume: {e!r}")   # print the error cause
            raise StopIteration

    def shutdown(self):
        pass
