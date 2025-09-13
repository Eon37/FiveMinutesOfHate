import http from 'k6/http';
import { sleep, check } from 'k6';

//const BASE_URL = 'https://five-minutes-of-hate.fly.dev/api';
const BASE_URL = 'http://localhost:8080/api';

export const options = {
  scenarios: {
    ramping_requests: {
      executor: 'ramping-arrival-rate',
      startRate: 5, // 5 iterations per second to start
      timeUnit: '1s',
      preAllocatedVUs: 50,
      maxVUs: 100,

      stages: [
        { target: 10, duration: '30s' },
        { target: 25, duration: '30s' },
        { target: 0, duration: '20s' },
      ],
    },
  },
};


export default function () {
  const jar = http.cookieJar();
  const headers = {
      'Content-Type': 'application/json',
    };

  const getRes = http.get(`${BASE_URL}/posts`, { jar });

  check(getRes, {
    'fetched posts': (r) => r.status === 200
  });

  const setCookieHeader = getRes.headers['Set-Cookie'];

  if (setCookieHeader) {
    const match = setCookieHeader.match(/clientId=([^;]+)/);
    if (match) {
      const clientId = match[1];
      jar.set(BASE_URL, 'clientId', clientId);
    }
  }

  // 1. Create a post
  const createRes = http.post(`${BASE_URL}/posts?text=sosal???????????????????&anonymous=false`, null, { headers, jar });

  check(createRes, {
    'post created': (r) => r.status === 200
  });

  console.log(createRes.status);
  const postIdMatch = createRes.body.match(/id=\"post-(\d+)\"/);

    let postId = null
    if (postIdMatch) {
      postId = postIdMatch[1];
      console.log(`Extracted postId: ${postId}`);
    } else {
      console.warn('No postId found in response!');
    }

  // 2. Like the post
  const likeRes = http.post(`${BASE_URL}/posts/${postId}/like-async`, null, {
    headers: { headers, jar }
  });

  check(likeRes, {
    'post liked': (r) => r.status === 200
  });

  // 3. Add a comment
  const commentRes = http.post(`${BASE_URL}/posts/${postId}/comments`, JSON.stringify({
    comment: 'Yes!'
  }),
    { headers, jar }
  );

  console.log(`Comment create status: ${commentRes.status}`)
  check(commentRes, {
    'comment added': (r) => r.status === 200
  });

  // 4. Get all posts
  check(http.get(`${BASE_URL}/posts`, { jar }), {
    'fetched posts': (r) => r.status === 200
  });

  sleep(1);
}
