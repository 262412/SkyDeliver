package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    void save(AddressBook addressBook);

    void deleteById(Long id);

    void update(AddressBook addressBook);

    void updateIsDefaultByUserId(AddressBook addressBook);

    AddressBook getById(Long id);

    List<AddressBook> list(AddressBook addressBook);
}
