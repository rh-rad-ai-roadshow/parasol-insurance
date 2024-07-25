
const promptfoo = require('promptfoo');
const matchesSimilarity  =promptfoo.assertions.matchesSimilarity;

const modelsConfig = require('./models-config');


module.exports.extendExpect = function () {
  expect.extend({
    async toMatchSemanticSimilarity(received, expected, threshold = 0.8) {
      const result = await matchesSimilarity(received, expected, threshold,0, modelsConfig.gradingConfig);
      const pass = received === expected || result.pass;
      if (pass) {
        return {
          message: () => `expected ${received} not to match semantic similarity with ${expected}`,
          pass: true,
        };
      } else {
        return {
          message: () =>
            `expected ${received} to match semantic similarity with ${expected}, but it did not. Reason: ${result.reason}`,
          pass: false,
        };
      }
    },
  });
}

module.exports.callModel = async function (prompt, systemPrompt =  "you are a polite agent" ) {

    const response = await promptfoo.evaluate({
      prompts: [prompt],
      providers:modelsConfig.getProviders(systemPrompt)
    });

    return response.results[0].response.output;

}
