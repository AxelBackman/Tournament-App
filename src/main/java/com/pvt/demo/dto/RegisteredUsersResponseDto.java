package com.pvt.demo.dto;

public class RegisteredUsersResponseDto {
    public Long userId;
    public String userName;
    public Long eventInstanceId;
    public String eventName;
    public String status;
    public String profilePictureUrl;

    public RegisteredUsersResponseDto(Long userId, String userName, Long eventInstanceId, String title, String status, String profilePictureUrl) {
        this.userId = userId;
        this.userName = userName;
        this.eventInstanceId = eventInstanceId;
        this.eventName = title;
        this.status = status;
        this.profilePictureUrl = profilePictureUrl;
    }

    public RegisteredUsersResponseDto() {
        
    }
}
