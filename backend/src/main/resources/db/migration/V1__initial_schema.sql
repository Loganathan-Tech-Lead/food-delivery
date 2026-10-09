CREATE TABLE restaurants (
    id UUID PRIMARY KEY,
    name VARCHAR(140) NOT NULL,
    description TEXT NOT NULL,
    cuisine VARCHAR(80) NOT NULL,
    rating NUMERIC(2, 1) NOT NULL CHECK (rating BETWEEN 0 AND 5),
    delivery_minutes INTEGER NOT NULL CHECK (delivery_minutes > 0),
    image_url TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE menu_items (
    id UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL REFERENCES restaurants(id),
    name VARCHAR(140) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    available BOOLEAN NOT NULL DEFAULT true,
    image_url TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_menu_items_restaurant ON menu_items(restaurant_id);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL REFERENCES restaurants(id),
    customer_id VARCHAR(120) NOT NULL,
    total NUMERIC(12, 2) NOT NULL CHECK (total >= 0),
    status VARCHAR(32) NOT NULL,
    payment_status VARCHAR(24) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_orders_customer_created ON orders(customer_id, created_at DESC);

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    menu_item_id UUID NOT NULL REFERENCES menu_items(id),
    dish_name VARCHAR(140) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(10, 2) NOT NULL CHECK (unit_price >= 0),
    line_total NUMERIC(12, 2) NOT NULL CHECK (line_total >= 0)
);
CREATE INDEX idx_order_items_order ON order_items(order_id);

CREATE TABLE deliveries (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE REFERENCES orders(id),
    partner_name VARCHAR(120) NOT NULL,
    partner_phone VARCHAR(40) NOT NULL,
    status VARCHAR(32) NOT NULL,
    latitude NUMERIC(9, 6) NOT NULL,
    longitude NUMERIC(9, 6) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE delivery_locations (
    id UUID PRIMARY KEY,
    delivery_id UUID NOT NULL REFERENCES deliveries(id) ON DELETE CASCADE,
    latitude NUMERIC(9, 6) NOT NULL,
    longitude NUMERIC(9, 6) NOT NULL,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_delivery_locations_delivery_time ON delivery_locations(delivery_id, recorded_at DESC);

CREATE TABLE outbox_events (
    event_id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    topic VARCHAR(120) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    published_at TIMESTAMPTZ,
    attempts INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX idx_outbox_unpublished ON outbox_events(created_at) WHERE published_at IS NULL;

CREATE TABLE processed_events (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(80) NOT NULL,
    order_id UUID NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO restaurants (id, name, description, cuisine, rating, delivery_minutes, image_url) VALUES
('11111111-1111-4111-8111-111111111111', 'Saffron & Stone', 'Slow-roasted spices, bright herbs, and neighborhood hospitality.', 'Modern Indian', 4.8, 28, 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1000&q=85'),
('22222222-2222-4222-8222-222222222222', 'Little Napoli', 'Hand-stretched dough and wood-fired pies from our open kitchen.', 'Italian', 4.7, 32, 'https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=1000&q=85'),
('33333333-3333-4333-8333-333333333333', 'Greenhouse Kitchen', 'Seasonal bowls built around local produce and satisfying grains.', 'Plant-based', 4.9, 24, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=1000&q=85'),
('44444444-4444-4444-8444-444444444444', 'Birdsong Chicken', 'Crispy, juicy chicken with thoughtful sides and house-made sauces.', 'American', 4.6, 26, 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?auto=format&fit=crop&w=1000&q=85');

INSERT INTO menu_items (id, restaurant_id, name, description, price, available, image_url) VALUES
('a1111111-1111-4111-8111-111111111111', '11111111-1111-4111-8111-111111111111', 'Butter chicken', 'Tomato, cashew, fenugreek, and charred naan.', 17.50, true, 'https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?auto=format&fit=crop&w=700&q=80'),
('a1111111-1111-4111-8111-111111111112', '11111111-1111-4111-8111-111111111111', 'Paneer tikka bowl', 'Tandoor paneer, cumin rice, pickled onion, mint chutney.', 15.00, true, 'https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=700&q=80'),
('a1111111-1111-4111-8111-111111111113', '11111111-1111-4111-8111-111111111111', 'Crispy samosas', 'Three potato and pea pastries with tamarind dip.', 7.50, true, 'https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=700&q=80'),
('a2222222-2222-4222-8222-222222222221', '22222222-2222-4222-8222-222222222222', 'Margherita', 'San Marzano tomato, fior di latte, basil.', 16.00, true, 'https://images.unsplash.com/photo-1579751626657-72bc17010498?auto=format&fit=crop&w=700&q=80'),
('a2222222-2222-4222-8222-222222222222', '22222222-2222-4222-8222-222222222222', 'Mushroom truffle', 'Wild mushrooms, mozzarella, thyme, truffle oil.', 19.00, true, 'https://images.unsplash.com/photo-1571407970349-bc81e7e96d47?auto=format&fit=crop&w=700&q=80'),
('a2222222-2222-4222-8222-222222222223', '22222222-2222-4222-8222-222222222222', 'Burrata salad', 'Creamy burrata, ripe tomato, basil, grilled sourdough.', 13.50, true, 'https://images.unsplash.com/photo-1608039829572-78524f79c4c7?auto=format&fit=crop&w=700&q=80'),
('a3333333-3333-4333-8333-333333333331', '33333333-3333-4333-8333-333333333333', 'Harvest grain bowl', 'Farro, roasted squash, kale, tahini, toasted seeds.', 16.50, true, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=700&q=80'),
('a3333333-3333-4333-8333-333333333332', '33333333-3333-4333-8333-333333333333', 'Green goddess wrap', 'Avocado, crispy chickpeas, herbs, lemon dressing.', 13.00, true, 'https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=700&q=80'),
('a3333333-3333-4333-8333-333333333333', '33333333-3333-4333-8333-333333333333', 'Citrus crunch salad', 'Shaved fennel, orange, almond, bitter greens.', 14.00, true, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=700&q=80'),
('a4444444-4444-4444-8444-444444444441', '44444444-4444-4444-8444-444444444444', 'Hot honey chicken', 'Crispy thigh, hot honey, slaw, soft brioche.', 15.50, true, 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?auto=format&fit=crop&w=700&q=80'),
('a4444444-4444-4444-8444-444444444442', '44444444-4444-4444-8444-444444444444', 'Buttermilk tenders', 'Four tenders, pepper gravy, seasoned fries.', 14.00, true, 'https://images.unsplash.com/photo-1562967914-608f82629710?auto=format&fit=crop&w=700&q=80'),
('a4444444-4444-4444-8444-444444444443', '44444444-4444-4444-8444-444444444444', 'Charred corn', 'Lime butter, smoked paprika, cotija, cilantro.', 6.50, true, 'https://images.unsplash.com/photo-1551754655-cd27e38d2076?auto=format&fit=crop&w=700&q=80');