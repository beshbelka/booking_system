package booking_system.service;

import booking_system.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public long getBookCount() {
        return bookRepository.count();
    }

}
