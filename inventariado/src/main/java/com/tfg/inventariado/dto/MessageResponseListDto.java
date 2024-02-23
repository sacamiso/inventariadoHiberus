package com.tfg.inventariado.dto;

import lombok.Data;

@Data
public class MessageResponseListDto<T> {

	private MessageResponseListDto() {}

    /** The success. */
    boolean success;

    /** The error. */
    String error;

    /** The message. */
    T message;
    
    int limit;
    
    int skip;
    
    int numTotal;

    /**
     * Return fail.
     *
     * @param <T> the generic type
     * @param message the message
     * @return the message response dto
     */
    public static <T> MessageResponseListDto<T> fail(String message) {
    	MessageResponseListDto<T> messageResponseDto = new MessageResponseListDto<>();
        messageResponseDto.setSuccess(false);
        messageResponseDto.setError(message);
        messageResponseDto.setMessage(null);
        messageResponseDto.setLimit(0);
        messageResponseDto.setSkip(0);
        messageResponseDto.setNumTotal(0);
        return messageResponseDto;
    }

    /**
     * Return success.
     *
     * @param <T> the generic type
     * @param content the content
     * @return the message response dto
     */
    public static <T> MessageResponseListDto<T> success(MessageResponseListDto<T> content, int skip, int limit, int numTotal) {
    	MessageResponseListDto<T> messageResponseDto = new MessageResponseListDto<>();
        messageResponseDto.setSuccess(true);
        messageResponseDto.setError(null);
        messageResponseDto.setMessage(content.getMessage());
        messageResponseDto.setLimit(limit);
        messageResponseDto.setSkip(skip);
        messageResponseDto.setNumTotal(numTotal);
        return messageResponseDto;
    }

    /**
     * Return success.
     *
     * @param <T> the generic type
     * @param content the content
     * @return the message response dto
     */
    public static <T> MessageResponseListDto<T> success(T content, int skip, int limit, int numTotal) {
    	MessageResponseListDto<T> messageResponseDto = new MessageResponseListDto<>();
        messageResponseDto.setSuccess(true);
        messageResponseDto.setError(null);
        messageResponseDto.setMessage(content);
        messageResponseDto.setLimit(limit);
        messageResponseDto.setSkip(skip);
        messageResponseDto.setNumTotal(numTotal);
        return messageResponseDto;
    }
}
