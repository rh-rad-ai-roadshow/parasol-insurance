import Fastify from 'fastify';
import fastifyWebsocket from '@fastify/websocket';
import fastifyStatic from '@fastify/static';
import fastifyEnv from '@fastify/env';
import path from 'node:path';
import { fileURLToPath } from 'url';
import fs from 'node:fs';
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

import claimsRoute from './claims-route.mjs';
import sqliteConnector from './sqlite-connector.mjs';

import { getModel, createChain, answerQuestion, resetSessions } from './ai.mjs';

const fastify = Fastify({
  logger: true
});

await fastify.register(fastifyEnv, {
  schema: {
    type: 'object'
  },
  dotenv: true
});

const webuiLocation = '../webui/dist';

fastify.register(fastifyStatic, {
  wildcard: false,
  root: path.join(__dirname, webuiLocation)
});

fastify.get('/*', (req, res) => {
  res.send(fs.createReadStream(path.join(__dirname, webuiLocation, 'index.html')));
})

fastify.register(sqliteConnector);
fastify.register(claimsRoute);
fastify.register(fastifyWebsocket);
fastify.register(async function (fastify) {
  fastify.get('/ws/query', { websocket: true }, (ws, req) => {
    const controller = new AbortController();

    ws.on('close', () => {
      resetSessions(ws);
      controller.abort();
      console.log('connection closed');
    });

    ws.on('error', console.error);

    ws.on('message', async (data) => {
      const stringData = data.toString();

      // This should be JSON
      let JSONmessage;
      try {
        JSONmessage = JSON.parse(stringData);
      } catch(err) {
        console.log(err);
      }

      console.log('Query from the Client', JSONmessage);

      console.log('Starting to Ask', new Date());

      try {
        const answerStream = await answerQuestion(JSONmessage, ws);

        for await (const chunk of answerStream) {
          console.log(`Got Chat Response: ${chunk.content}`);

          //'{"type":"token","token":" Hello","source":""}'
          const formattedAnswer = {
            type: 'token',
            token: chunk.content,
            source: ''
          };

          ws.send(JSON.stringify(formattedAnswer));
        }
      } catch (err) {
        console.log(err);
      }

      console.log('Done Asking', new Date());
    });

    // AI Related Setup
    const model = getModel().bind({ signal: controller.signal });
    createChain(model);
  });
});

/**
 * Run the server!
 */
const start = async () => {
  try {
    await fastify.listen({ port: process.env.PORT || 8005 })
  } catch (err) {
    fastify.log.error(err)
    process.exit(1)
  }
};
start();
