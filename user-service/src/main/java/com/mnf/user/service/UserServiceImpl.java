package com.mnf.user.service;

import com.mnf.common.entity.UserEntity;
import com.mnf.component.BaseService;
import com.mnf.component.dto.ResponseDto;
import com.mnf.component.dto.ResponseStatusOnlyDto;
import com.mnf.component.enumeration.ResponseDtoStatusEnum;
import com.mnf.user.dto.AddUserRequestDto;
import com.mnf.user.dto.UserResponseDto;
import com.mnf.user.exception.UserException;
import com.mnf.user.repository.IUserRepository;
import com.mnf.user.util.PasswordUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl extends BaseService implements IUserService{
    @Autowired
    IUserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ResponseDto<UserResponseDto> findOneById(String id) {
        ResponseDto<UserResponseDto> responseDto = new ResponseDto<>();
        responseDto.status = ResponseDtoStatusEnum.SUCCESS;
        responseDto.message = "OK";

        Optional<UserEntity> optionalUserEntity = userRepository.findById(id);

        if(optionalUserEntity.isPresent()){
            responseDto.setContent(modelMapper.map(optionalUserEntity.get(), UserResponseDto.class));
        }else{
            responseDto.message = UserException.NOT_FOUND;
            logger.info("user not found");
        }

        return responseDto;
    }

    @Override
    public ResponseStatusOnlyDto addUser(AddUserRequestDto requestDto) {
        ResponseStatusOnlyDto responseDto = new ResponseStatusOnlyDto();
        responseDto.status = ResponseDtoStatusEnum.SUCCESS;
        responseDto.message = "OK";

        Optional<UserEntity> existingUser = userRepository.findByUsername(requestDto.getUsername());

        if(existingUser.isPresent()){
            responseDto.message = UserException.USERNAME_EXISTED;
        }else{
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(requestDto.getUsername());
            userEntity.setPassword(PasswordUtil.hashPassword(requestDto.getPassword()));
            userRepository.save(userEntity);
        }

        return responseDto;
    }
}
