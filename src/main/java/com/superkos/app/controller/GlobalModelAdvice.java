package com.superkos.app.controller;

import com.superkos.app.model.PencariHunian;
import com.superkos.app.model.User;
import com.superkos.app.repository.MessageRepository;
import com.superkos.app.repository.PencariHunianRepository;
import com.superkos.app.repository.RoommateRequestRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @Autowired
    private PencariHunianRepository pencariHunianRepository;

    @Autowired
    private RoommateRequestRepository requestRepository;

    @Autowired
    private MessageRepository messageRepository;

    @ModelAttribute
    public void addGlobalAttributes(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser instanceof PencariHunian pencari) {
            PencariHunian fresh = pencariHunianRepository.findById(pencari.getId()).orElse(null);
            if (fresh != null) {
                long pendingRequests = requestRepository.countByTargetPencariAndStatus(fresh, "PENDING");
                long acceptedUnread = requestRepository.countByPencariHunianAndStatusAndSenderRead(fresh, "ACCEPTED", false);
                long unreadMessages = messageRepository.countUnreadMessages(fresh);

                long totalInboxNotifications = pendingRequests + acceptedUnread + unreadMessages;
                model.addAttribute("inboxNotificationCount", totalInboxNotifications);
            } else {
                model.addAttribute("inboxNotificationCount", 0L);
            }
        } else {
            model.addAttribute("inboxNotificationCount", 0L);
        }
    }
}
