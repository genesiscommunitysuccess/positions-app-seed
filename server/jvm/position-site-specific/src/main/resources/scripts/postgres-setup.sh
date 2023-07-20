if [ -f /etc/redhat-release ];
then
  if systemctl | grep -q postgres
  then
    echo "Postgres already installed";
  else
    echo "Installing postgresql 15";
    # Install the repository RPM:
    sudo yum install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm --nogpgcheck

    # Install PostgreSQL:
    sudo yum install -y postgresql15-server

    # Optionally initialize the database and enable automatic start:
    sudo /usr/pgsql-15/bin/postgresql-15-setup initdb
    sudo systemctl enable postgresql-15
    sudo systemctl start postgresql-15
  fi

  sudo -u postgres psql postgres << EOF
  ALTER USER postgres WITH PASSWORD 'mysecretpassword';
  CREATE TABLE IF NOT EXISTS intable (trade_id SERIAL PRIMARY KEY, genesis_trade_id VARCHAR(20), instrument_id VARCHAR(20) NOT NULL, counterparty_id VARCHAR(20) NOT NULL, quantity INT NOT NULL, side VARCHAR(5) NOT NULL, price REAL NOT NULL);
  CREATE TABLE IF NOT EXISTS outtable (trade_id VARCHAR(20), genesis_trade_id VARCHAR(20) PRIMARY KEY, instrument_id VARCHAR(20) NOT NULL, counterparty_id VARCHAR(20) NOT NULL, quantity INT NOT NULL, side VARCHAR(5) NOT NULL, price REAL NOT NULL);
  ALTER TABLE intable REPLICA IDENTITY FULL;
  INSERT INTO intable (instrument_id, counterparty_id, quantity, side, price) VALUES (1, 1, 100, 'BUY', 49) ON CONFLICT DO NOTHING;
  INSERT INTO intable (instrument_id, counterparty_id, quantity, side, price) VALUES (2, 2, 37, 'BUY', 920) ON CONFLICT DO NOTHING;
  INSERT INTO intable (instrument_id, counterparty_id, quantity, side, price) VALUES (2, 2, 3, 'SELL', 950) ON CONFLICT DO NOTHING;
  EOF
else
  echo "Script not tested outside of CentOS7"
fi
