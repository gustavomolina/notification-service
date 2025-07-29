-- Insert initial users
INSERT INTO users (name, email, phone_number) VALUES
    ('John Doe', 'john.doe@example.com', '+1234567890'),
    ('Jane Smith', 'jane.smith@example.com', '+1987654321'),
    ('Bob Johnson', 'bob.johnson@example.com', '+1122334455'),
    ('Alice Williams', 'alice.williams@example.com', '+1555666777'),
    ('Charlie Brown', 'charlie.brown@example.com', '+1999888777');

-- User 1: John Doe - Subscribed to Sports and Movies, prefers Email and SMS
INSERT INTO user_subscriptions (user_id, category) VALUES
    (1, 'SPORTS'),
    (1, 'MOVIES');
INSERT INTO user_channels (user_id, channel) VALUES
    (1, 'EMAIL'),
    (1, 'SMS');

-- User 2: Jane Smith - Subscribed to Finance and Movies, prefers Email and Push Notification
INSERT INTO user_subscriptions (user_id, category) VALUES
    (2, 'FINANCE'),
    (2, 'MOVIES');
INSERT INTO user_channels (user_id, channel) VALUES
    (2, 'EMAIL'),
    (2, 'PUSH_NOTIFICATION');

-- User 3: Bob Johnson - Subscribed to Sports and Finance, prefers SMS only
INSERT INTO user_subscriptions (user_id, category) VALUES
    (3, 'SPORTS'),
    (3, 'FINANCE');
INSERT INTO user_channels (user_id, channel) VALUES
    (3, 'SMS');

-- User 4: Alice Williams - Subscribed to all categories, prefers all channels
INSERT INTO user_subscriptions (user_id, category) VALUES
    (4, 'SPORTS'),
    (4, 'FINANCE'),
    (4, 'MOVIES');
INSERT INTO user_channels (user_id, channel) VALUES
    (4, 'EMAIL'),
    (4, 'SMS'),
    (4, 'PUSH_NOTIFICATION');

-- User 5: Charlie Brown - Subscribed to Movies only, prefers Email and Push Notification
INSERT INTO user_subscriptions (user_id, category) VALUES
    (5, 'MOVIES');
INSERT INTO user_channels (user_id, channel) VALUES
    (5, 'EMAIL'),
    (5, 'PUSH_NOTIFICATION');

-- Insert a sample message for each category
INSERT INTO messages (category, content, created_at) VALUES
    ('SPORTS', 'Breaking news: Local team wins championship!', NOW() - INTERVAL '1 hour'),
    ('FINANCE', 'Market update: Stocks reach all-time high', NOW() - INTERVAL '2 hours'),
    ('MOVIES', 'New blockbuster movie to be released next week', NOW() - INTERVAL '3 hours');