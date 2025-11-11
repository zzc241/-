package ruiji.ruiji.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import ruiji.ruiji.mapper.AddressBookMapper;
import ruiji.ruiji.service.AddressBookService;
import ruiji.ruiji.pojo.AddressBook;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper , AddressBook> implements AddressBookService{
    
}
