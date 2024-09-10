module.exports.gradingConfig = {
  provider: {
    embedding: {
      id: 'openai:embedding:local',
      config: {
      apiBaseUrl: process.env['EMBEDDING_MODEL_URL'],
      apiKey: 'local-service'
      }
  }
  }
};
module.exports.getProviders = (systemPrompt) => {
  return [{
    id: process.env['PARASOL_MODEL_URL'],
    config: {
       method: 'POST',
       headers: {'Content-Type': 'application/json'},
       body: {
        model: process.env['PARASOL_MODEL_NAME'],
        max_tokens: 256,
         messages : [   
           {
             content: systemPrompt,
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
   
}]
  
}
