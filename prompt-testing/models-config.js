module.exports.gradingConfig = {
    provider: {
      embedding: {
        id: 'openai:embedding:local',
        config: {
        apiBaseUrl: 'http://localhost:9999/v1',
        apiKey: 'local-service'
        }
    }
    }
  };
  module.exports.providers = [{
      id: 'https://xxx:443/v1/chat/completions',
      config: {
         method: 'POST',
         headers: {'Content-Type': 'application/json'},
         body: {
           model: "phi3",
           messages : [   
             {
               content: 'You are a helpful assistant.',
               role: 'system'
             },
             {
               content: '{{prompt}}',
               role: 'user'
             }
             ]
          },
          responseParser: 'json.choices[0].message.content'
      }
     
  }];
  