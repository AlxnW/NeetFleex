# Chain of Responsibility Pattern

## Overview
The Chain of Responsibility pattern is a behavioral design pattern that lets you pass requests along a chain of handlers. Upon receiving a request, each handler decides either to process the request or to pass it to the next handler in the chain.

## Implementation in NeetFlex

In the NeetFlex application, the Chain of Responsibility pattern is used to filter content based on various criteria:

1. **Subscription-based filtering**: Filters content based on the user's subscription type (Basic, Premium, Family)
2. **Genre-based filtering**: Filters content based on specified genres
3. **Content type filtering**: Filters content based on whether it's a movie or series

### Key Components

- **ContentFilter (Interface)**: Defines the interface for all content filters
- **AbstractContentFilter**: Provides common functionality for all concrete filters
- **SubscriptionBasedFilter**: Filters content based on subscription type
- **GenreBasedFilter**: Filters content based on genre
- **ContentTypeFilter**: Filters content based on content type
- **ContentFilterChain**: Manages the chain of filters

### How It Works

1. The client creates a `ContentFilterChain` and adds filters to it
2. Each filter in the chain processes the content list and passes the result to the next filter
3. The final result is the content list after all filters have been applied

### Example Usage

```java
// Create the filter chain
ContentFilterChain filterChain = new ContentFilterChain();

// Add filters to the chain
filterChain.addFilter(new SubscriptionBasedFilter());
filterChain.addFilter(new ContentTypeFilter(ContentType.MOVIE));
filterChain.addFilter(new GenreBasedFilter(List.of("Action", "Comedy")));

// Apply the filter chain
List<Content> filteredContent = filterChain.applyFilters(allContent, user);
```

## API Endpoint

The Chain of Responsibility pattern is exposed through the following API endpoint:

```
GET /api/content/filter?username={username}&contentType={contentType}&genres={genres}
```

### Parameters

- **username** (required): The username of the user requesting the content
- **contentType** (optional): The type of content to filter (MOVIE or SERIES)
- **genres** (optional): The list of genres to filter by, comma-separated

### Examples

1. Filter movies based on John's subscription level:
   ```
   GET /api/content/filter?username=john&contentType=MOVIE
   ```

2. Filter content in Action and Comedy genres based on Jane's subscription level:
   ```
   GET /api/content/filter?username=jane&genres=Action,Comedy
   ```

3. Filter series in Drama genre based on Bob's subscription level:
   ```
   GET /api/content/filter?username=bob&contentType=SERIES&genres=Drama
   ```

## Benefits

- **Decoupling**: Each filter is independent and can be added or removed without affecting other filters
- **Flexibility**: Filters can be combined in different ways to create different filtering behaviors
- **Single Responsibility**: Each filter has a single responsibility, making the code easier to maintain
- **Open/Closed Principle**: New filters can be added without modifying existing code