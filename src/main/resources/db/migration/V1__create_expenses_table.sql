  CREATE TABLE expenses (
      id UUID PRIMARY KEY,
      amount DECIMAL(10, 2) NOT NULL,
      category VARCHAR(50) NOT NULL,
      description VARCHAR(255),
      date DATE NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  );