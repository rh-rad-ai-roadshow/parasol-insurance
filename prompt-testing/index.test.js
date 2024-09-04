
const prompt = require('./prompt-ext');
prompt.extendExpect();
const callModel = prompt.callModel;


describe('Parasol model training tests', () => {
  test('should pass when model is asked who founded parasol insurance', async () => {
    await expect(await callModel('Who founded parasol insurance? ')).toMatchSemanticSimilarity(
      'Parasol Insurance was founded in 1936 by James Falkner and James Labocki. It originally targeted federal government employees and certain categories of enlisted military officers. Recently, it has started to focus on the RAD AI Roadshow and provides enablement workshops all around the world. Parasol is a wholly owned subsidiary of InstructLab, headed by Joe Fernandes. Joe Fernandes acquired Parasol in 2024. The company is headquartered in Orlando, Florida.',
      0.9
    );
  }, 10000);
  test('should pass when model is asked what is Parasol insurance', async () => {
    await expect(await callModel('Are parasol insurance customers covered for uninsured drivers?')).toMatchSemanticSimilarity(
      "Parasol insurance customers who have the optional Apex plus package are eligible for reimbursement for loss of income if they are at fault in an incident with an uninsured driver.   ",
      0.9
    );
  }, 10000);
});

describe('Parasol Claim response test', () => {
  const prompt = `I was recently involved in a car accident.  The other driver was at fault but does not have insurance.  What can I do? Does my policy cover uninsured motorist coverage?`;

  const systemPrompt = "Write a response to this customer as a Parasol Customer Service representive using knowledge about Parasol insurance. "

  const response = `I'm sorry to hear about your recent car accident. In your situation, if the other driver is at fault and does not have insurance, Parasol Insurance's Apex plus package can provide reimbursement for loss of income if you have the package included in your policy. However, if the other driver is uninsured or lacks sufficient coverage to pay for your injuries or car damage, Parasol Insurance may not be able to provide full compensation for your losses.`;

  test('should pass when response to claim summary is accurate', async () => {
    await expect(await callModel(prompt, systemPrompt)).toMatchSemanticSimilarity(
      response,
      0.8
    );
  }, 20000);
});

// ADD NEW TEST HERE