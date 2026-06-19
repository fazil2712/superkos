package com.superkos.app.controller;

import com.superkos.app.model.PemilikProperti;
import com.superkos.app.model.PencariHunian;
import com.superkos.app.model.User;
import com.superkos.app.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
// #babas(Notifikasi Sistem)
@ControllerAdvice
public class GlobalModelAdvice {

    @Autowired
    private PencariHunianRepository pencariHunianRepository;

    @Autowired
    private PemilikPropertiRepository pemilikPropertiRepository;

    @Autowired
    private RoommateRequestRepository requestRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ReservasiRepository reservasiRepository;

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
                
                
                java.util.List<String> notificationsList = fresh.popnotif(requestRepository);
                model.addAttribute("notificationsList", notificationsList);
            } else {
                model.addAttribute("inboxNotificationCount", 0L);
                model.addAttribute("notificationsList", new java.util.ArrayList<String>());
            }

        } else if (loggedInUser instanceof PemilikProperti pemilik) {
            PemilikProperti fresh = pemilikPropertiRepository.findById(pemilik.getId()).orElse(null);
            if (fresh != null) {
                long pendingReservasi = reservasiRepository.countByPemilikAndStatus(fresh, "PENDING");
                long unreadReservasi  = reservasiRepository.countByPemilikAndPemilikRead(fresh, false);
                long unreadMessages   = messageRepository.countUnreadMessages(fresh);

                long totalNotifications = pendingReservasi + unreadMessages;
                model.addAttribute("inboxNotificationCount", totalNotifications);
                model.addAttribute("pemilikPendingCount", pendingReservasi);
                model.addAttribute("pemilikUnreadReservasi", unreadReservasi);
            } else {
                model.addAttribute("inboxNotificationCount", 0L);
                model.addAttribute("pemilikPendingCount", 0L);
                model.addAttribute("pemilikUnreadReservasi", 0L);
            }
            model.addAttribute("notificationsList", new java.util.ArrayList<String>());

        } else {
            model.addAttribute("inboxNotificationCount", 0L);
            model.addAttribute("notificationsList", new java.util.ArrayList<String>());
            model.addAttribute("pemilikPendingCount", 0L);
            model.addAttribute("pemilikUnreadReservasi", 0L);
        }
    }
}
// #/babas(Notifikasi Sistem)
