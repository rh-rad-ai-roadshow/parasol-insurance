import fp from 'fastify-plugin';
import sqlite from 'sqlite3';
import fs from 'node:fs/promises';

async function sqliteConnection(fastify, options) {
  const db = new sqlite.Database(':memory:');
  // read the sql lite file
  const file = await fs.readFile('./import.sql');

  const dataArr = file.toString().split(/;\n/);
  const createTable = `CREATE TABLE claim(
  id INTEGER PRIMARY KEY,
  claim_number TEXT NOT NULL,
  category TEXT NOT NULL,
  policy_number TEXT NOT NULL,
  inception_date DATETIME NOT NULL,
  client_name TEXT NOT NULL,
  email_address TEXT NOT NULL,
  subject TEXT NOTE NULL,
  body TEXT NOTE NULL,
  summary TEXT NOTE NULL,
  location TEXT NOTE NULL,
  claim_time TEXT NOTE NULL,
  sentiment TEXT NOTE NULL,
  status TEXT NOTE NULL
  )`;

  db.serialize(() => {

    // Create the Table
    db.exec(createTable);

    db.parallelize(() => {
      for (let query of dataArr) {
        if (query && !query.includes('ALTER SEQUENCE')) {
          query = query.trim();
          query += ';'

          db.exec(query);
        }
      }
    });
  });
  fastify.decorate('sqlite', db);
}

export default fp(sqliteConnection, {
  name: 'sqllite-connector'
});