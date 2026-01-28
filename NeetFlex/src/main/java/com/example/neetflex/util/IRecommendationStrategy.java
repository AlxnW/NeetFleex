package com.example.neetflex.util;


import com.example.neetflex.model.user.User;

import javax.swing.text.AbstractDocument;
import java.util.List;

public interface IRecommendationStrategy {

    List<AbstractDocument.Content> recommend(User user);
}
