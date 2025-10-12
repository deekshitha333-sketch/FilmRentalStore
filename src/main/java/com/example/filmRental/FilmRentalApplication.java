package com.example.filmRental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.example" 
})
public class FilmRentalApplication {
  public static void main(String[] args) {
    SpringApplication.run(FilmRentalApplication.class, args);
  }
}

