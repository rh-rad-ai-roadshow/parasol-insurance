
const prompt = require('./prompt-ext');
prompt.extendExpect();
const callModel = prompt.callModel;


describe('Parasol model training tests', () => {
  test('should pass when model is asked what an insurance policy', async () => {
    await expect(await callModel('What is an insurance policy')).toMatchSemanticSimilarity(
      'An insurance policy is a contract between an individual or entity (the insured) and an insurance company (the insurer). The contract outlines the terms, conditions, and coverage provided by the insurer in exchange for premium payments made by the insured. The policy defines the risks that are covered, the limits and exclusions of coverage, deductibles, and any other relevant details. In the event of a covered loss, the insurer is obligated to provide financial compensation to the insured, as specified in the policy.',
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

describe('Parasol Claim response test', () => {
  const prompt = `On January 2nd, 1955, at around 3:30 PM, a car accident occurred at the intersection of Colima Road and Azusa Avenue in Hill Vallet. The involved parties were Marty McFly, driving a silver Delorean DMC-12 (OUTA-TIME), and Biff Tanner in a blue Type 2 Volkswagen Bus (BIF-RULZ).

  Marty was heading south on Colima Road when Biff failed to stop at the red traffic signal on Asuza Avenue, causing a collision with Marty''s vehicle. Both drivers exchanged information and took photos of the accident scene, which included damages to the front driver and passenger side of Marty''s Delorean DMC-12 and the front driver''s side of Biff''s Volkswagen Bus. No injuries were reported.

  Marty has attached necessary documents, such as photos, a police report, and an estimate for repair costs, to his email. He requests prompt attention to the claim and is available at (916) 555-4385 or marty.mcfly@email.com for any additional information or documentation needed.`;

  const systemPrompt = "You are a helpful, respectful and honest assistant named 'Parasol Assistant'. You will be given a claim summary, references to provide you with information, and a question. You must answer the question based as much as possible on this claim with the help of the references. Always answer as helpfully as possible, while being safe. Your answers should not include any harmful, unethical, racist, sexist, toxic, dangerous, or illegal content. Please ensure that your responses are socially unbiased and positive in nature. If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information."

  const response = `Based on the claim summary provided, the accident occurred on January 2nd, 1955, at the intersection of Colima Road and Azusa Avenue in Hill Vallet. Marty McFly, driving a silver Delorean DMC-12, was heading south on Colima Road, and Biff Tanner, in a blue Type 2 Volkswagen Bus, failed to stop at the red signal on Asuza Avenue, resulting in a collision with Marty's vehicle.

  Both drivers exchanged their contact information, took photos of the accident scene and the damages to their vehicles, and no injuries were reported. Marty has attached necessary documentation to his claim, including photos, a police report, and an estimate for repair costs. He is available at (916) 555-4385 or marty.mcfly@email.com for further information or documentation if required.`;

  test('should pass when response to claim summary is accurate', async () => {
    await expect(await callModel(prompt, systemPrompt)).toMatchSemanticSimilarity(
      response,
      0.8
    );
  }, 20000);
});