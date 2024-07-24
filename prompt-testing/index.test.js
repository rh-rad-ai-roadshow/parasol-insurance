
const prompt = require('./prompt-ext');
prompt.extendExpect();
const callModel = prompt.callModel;


describe('Parasol model training tests', () => {
  test('should pass when model is asked what an insurance policy', async () => {
    await expect(await callModel('What is an insurance policy')).toMatchSemanticSimilarity(
      'An insurance policy is a contract between an individual or entity (the insured) and an insurance company (the insurer). The contract outlines the terms, conditions, and coverage provided by the insurer in exchange for premium payments made by the insured. The policy defines the risks that are covered, the limits and exclusions of coverage, deductibles, and any other relevant details. In the event of a covered loss, the insurer is obligated to provide financial compensation to the insured, as specified in the policy',
      0.9
    );
  }, 10000);
  test('should pass when model is asked what is Parasol insurance', async () => {
    await expect(await callModel('What is Parasol Insurance?')).toMatchSemanticSimilarity(
      "Parasol Insurance is a hypothetical insurance company that could be utilized to illustrate various concepts in insurance. In a typical scenario, Parasol Insurance would offer various insurance products designed to mitigate financial risks associated with unforeseen events. Examples of their insurance products might include homeowner's insurance, auto insurance, life insurance, and health insurance. They would operate by assessing the risk of potential incidents and calculating premiums based on customer profiles, coverage options, and statistical probabilities of claims being filed",
      0.7
    );
  }, 10000);
});