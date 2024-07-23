import config from '@app/config';
import { Button, Form, FormGroup, TextArea, Spinner } from '@patternfly/react-core';
import * as React from 'react';
import { useState } from 'react';

interface ResponseData {
    subject: string;
    message: string;
  }

const EmailGenerate: React.FunctionComponent = () => {

    const [text, setText] = useState('');
    const [error, setError] = useState('');
    const [response, setResponse] = useState<ResponseData | null>(null);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (event: React.FormEvent) => {
      event.preventDefault();
      setLoading(true);

      try {
        const response = await fetch('/api/email', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ text: text })
        });
        console.log("response: ");
        console.log(response);
        if (response.ok) {
          const data = await response.json();
          setResponse(data);
          setError('');
        } else {
          setError('Error: Failed to fetch response from server.');
        }
      } catch (error) {
        setError('Error!');
      } finally {
        setLoading(false);
      }

    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>

        <Form onSubmit={handleSubmit} style={{ width: '600px', textAlign: 'center' }}>
        <FormGroup label="Your Feedback" fieldId="text-area">
          <TextArea
            onChange={(value) => setText(value.target.value)}
            name="textArea"
            id="text-area"
            aria-label="Your feedback"
            placeholder="Enter customer email content here"
            style={{ width: '100%' }}
          />
        </FormGroup>
        <Button type="submit">Submit</Button>
        {loading && <Spinner style={{justifyContent: 'center', alignItems: 'center'}} size="lg" />}
        {response && (
          <div id="email-response" style={{ marginTop: '20px', color: 'blue', textAlign: 'left', width: '100%' }}>
            <div id="email-response-subject" style={{ fontWeight: 'bold', fontFamily: 'Arial' }}>Subject: {response.subject}</div>
            <pre id="email-response-message" style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word', fontFamily: 'monospace', border: '1px solid black', padding: '10px' }}>
              {response.message}
            </pre>
          </div>
        )}

        {error && (
            <div style={{ marginTop: '20px', color: 'red', textAlign: 'left', width: '100%' }}>{error}</div>
        )}

      </Form>
    </div>

    )
}

export { EmailGenerate };
