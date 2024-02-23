package com.tfg.inventariado.dto;

import lombok.Data;

@Data
public class MessageResponseDto<T> {

	private MessageResponseDto() {}

    /** The success. */
    boolean success;

    /** The error. */
    String error;

    /** The message. */
    T message;

    /**
     * Return fail.
     *
     * @param <T> the generic type
     * @param message the message
     * @return the message response dto
     */
    public static <T> MessageResponseDto<T> fail(String message) {
        MessageResponseDto<T> messageResponseDto = new MessageResponseDto<>();
        messageResponseDto.setSuccess(false);
        messageResponseDto.setError(message);
        messageResponseDto.setMessage(null);
        return messageResponseDto;
    }

    /**
     * Return success.
     *
     * @param <T> the generic type
     * @param content the content
     * @return the message response dto
     */
    public static <T> MessageResponseDto<T> success(MessageResponseDto<T> content) {
        MessageResponseDto<T> messageResponseDto = new MessageResponseDto<>();
        messageResponseDto.setSuccess(true);
        messageResponseDto.setError(null);
        messageResponseDto.setMessage(content.getMessage());
        return messageResponseDto;
    }

    /**
     * Return success.
     *
     * @param <T> the generic type
     * @param content the content
     * @return the message response dto
     */
    public static <T> MessageResponseDto<T> success(T content) {
        MessageResponseDto<T> messageResponseDto = new MessageResponseDto<>();
        messageResponseDto.setSuccess(true);
        messageResponseDto.setError(null);
        messageResponseDto.setMessage(content);
        return messageResponseDto;
    }
}
