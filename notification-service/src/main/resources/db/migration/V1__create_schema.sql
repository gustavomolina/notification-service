-- Create users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(50)
);

-- Create user_subscriptions table (many-to-many relationship between users and categories)
CREATE TABLE user_subscriptions (
    user_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, category),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create user_channels table (many-to-many relationship between users and channels)
CREATE TABLE user_channels (
    user_id BIGINT NOT NULL,
    channel VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, channel),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create messages table
CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create notifications table
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    channel VARCHAR(50) NOT NULL,
    sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    sent_at TIMESTAMP,
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_user_subscriptions_user_id ON user_subscriptions(user_id);
CREATE INDEX idx_user_channels_user_id ON user_channels(user_id);
CREATE INDEX idx_notifications_message_id ON notifications(message_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_sent ON notifications(sent);
CREATE INDEX idx_messages_category ON messages(category);
CREATE INDEX idx_messages_created_at ON messages(created_at);