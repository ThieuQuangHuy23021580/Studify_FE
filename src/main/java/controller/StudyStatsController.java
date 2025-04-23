package controller;

import backend.models.User;

public class StudyStatsController {
    private User user;

    public void initData(User user) {
        if (user != null) {
            this.user = user;
        }
    }
}
