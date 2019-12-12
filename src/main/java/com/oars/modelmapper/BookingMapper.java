package com.oars.modelmapper;

import com.oars.dto.BookingDto;
import com.oars.entity.Booking;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class BookingMapper {

    @Autowired
    private ModelMapper modelMapper;

    @PostConstruct
    public void addMappings() {
        modelMapper.createTypeMap(Booking.class, BookingDto.class).addMappings(new PropertyMap<Booking,
                BookingDto>() {
            @Override
            protected void configure() {
                skip(destination.getFlight());
                skip(destination.getUser());
            }
        });

        modelMapper.createTypeMap(BookingDto.class, Booking.class).addMappings(new PropertyMap<BookingDto,
                Booking>() {
            @Override
            protected void configure() {
                skip(destination.getFlight());
                skip(destination.getUser());
            }
        });
    }

    /**
     * Convert {@link Booking} entity to {@link BookingDto }
     *
     * @param booking
     * @return {@link BookingDto }
     */
    public BookingDto convertToDto(Booking booking) {
        return modelMapper.map(booking, BookingDto.class);
    }

    /**
     * Convert {@link BookingDto} entity to {@link Booking }
     *
     * @param bookingDto
     * @return {@link Booking }
     */
    public Booking convertToEntity(BookingDto bookingDto) {
        return modelMapper.map(bookingDto, Booking.class);
    }

    /**
     * Merge {@link BookingDto} entity to {@link Booking }
     *
     * @param bookingDto
     * @return {@link Booking }
     */
    public void mergeToEntity(BookingDto bookingDto, Booking booking) {
        modelMapper.map(bookingDto, booking);
    }
}
