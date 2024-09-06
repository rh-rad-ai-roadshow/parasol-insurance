
const prompt = require('./prompt-ext');
prompt.extendExpect();
const callModel = prompt.callModel;


describe('Parasol model training tests', () => {
  test('should pass when model is asked what an insurance policy', async () => {
    await expect(await callModel('What is an insurance policy? ')).toMatchSemanticSimilarity(
      'An insurance policy is a contract between an individual or entity (the insured) and an insurance company (the insurer). The contract outlines the terms, conditions, and coverage provided by the insurer in exchange for premium payments made by the insured. The policy defines the risks that are covered, the limits and exclusions of coverage, deductibles, and any other relevant details. In the event of a covered loss, the insurer is obligated to provide financial compensation to the insured, as specified in the policy.',
      0.9
    );
  }, 10000);
  test('should pass when model is asked who founded Parasol insurance', async () => {
    await expect(await callModel('In one sentence, who founded Parasol Insurance? ')).toMatchSemanticSimilarity(
      "Parasol Insurance was founded in 1936 by James Falkner and James Labocki.",
      0.9
    );
  }, 10000);
});

// ADD NEW TEST HERE