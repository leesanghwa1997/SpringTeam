package com.busanit501.bootproject.service;

import com.busanit501.bootproject.domain.MatchingRoom;
import com.busanit501.bootproject.domain.Message;
import com.busanit501.bootproject.domain.User;
import com.busanit501.bootproject.dto.MessageDTO;
import com.busanit501.bootproject.repository.MatchingRoomRepository;
import com.busanit501.bootproject.repository.MessageRepository;
import com.busanit501.bootproject.repository.RoomParticipantsRepository;
import com.busanit501.bootproject.repository.UserRepostiory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {
    @Autowired
    private final MessageRepository messageRepository;
    @Autowired
    private UserRepostiory userRepostiory;
    @Autowired
    private RoomParticipantsRepository roomParticipantsRepository;
    @Autowired
    private MatchingRoomRepository matchingRoomRepository;
    private final ModelMapper modelMapper;

    @Override
    public int addMessage(MessageDTO messageDTO) {
        Message message = modelMapper.map(messageDTO, Message.class);

        User sender = userRepostiory.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid senderId: " + messageDTO.getSenderId()));
        MatchingRoom chatRoom = matchingRoomRepository.findById(messageDTO.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid senderId: " + messageDTO.getChatRoomId()));
        message.setSender(sender);
        message.setChatRoom(chatRoom);

        Message savedMessage = messageRepository.save(message);
        return savedMessage.getMessageId();
    }

    @Override
    public void updateMessage(MessageDTO messageDTO) {
        Optional<Message> message = messageRepository.findById(messageDTO.getMessageId());
        Message savedMessage = message.orElseThrow();
        savedMessage.MessageUpdate(messageDTO.getContent());
        messageRepository.save(savedMessage);
    }

    @Override
    public void deleteMessage(int messageId) {
        messageRepository.deleteById(messageId);
    }

    @Override
    public List<MessageDTO> searchMessage(String keyword, int roodId) {
        List<Message> messages = messageRepository.searchMessageByMatchingRoomId(keyword, roodId);

        List<MessageDTO> dtoList = new ArrayList<>();
        for(Message message : messages) {
            MessageDTO dto = MessageDTO.builder()
                    .messageId(message.getMessageId())
                    .chatRoomId(message.getChatRoom().getRoomId())
                    .senderId(message.getSender().getUserId())
                    .content(message.getContent())
                    .sentAt(message.getSentAt())
                    .isRead(message.isRead())
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }
}
