async function claimsRoute (fastify, options) {
  fastify.get('/api/db/claims', (request, reply) => {
    fastify.sqlite.all('select * from claim', (err, rows) => {
      return reply.send(rows);
    });
  });

  fastify.get('/api/db/claims/:id', (request, reply) => {
    fastify.sqlite.all('select * from claim where id=?', request.params.id, (err, rows) => {
      return reply.send(rows[0]);
    });
  });
}

export default claimsRoute;