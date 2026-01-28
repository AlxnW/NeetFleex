package com.example.neetflex.patterns.adapter;

import com.example.neetflex.model.contents.Content;

public interface IContentProvider {
    String getContent(String id);
    Content findContentbyId(String id);
}
