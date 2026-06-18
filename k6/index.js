import http from 'k6/http';
import { Rate } from 'k6/metrics';

const failureRate = new Rate('failed_requests');

const BASE_URL = 'http://localhost:8080';

const titles = ['Clean Code', 'The Pragmatic Programmer', 'Design Patterns', 'Refactoring', 'Domain-Driven Design'];
const authors = ['Robert C. Martin', 'Andrew Hunt', 'Gang of Four', 'Martin Fowler', 'Eric Evans'];

export function test_books_config() {
    const usePost = Math.random() < 0.5;

    if (usePost) {
        const index = Math.floor(Math.random() * titles.length);
        const payload = JSON.stringify({
            title: `${titles[index]} ${Date.now()}`,
            author: authors[index],
        });

        const res = http.post(`${BASE_URL}/books`, payload, {
            headers: { 'Content-Type': 'application/json' },
        });

        failureRate.add(res.status !== 201);
    } else {
        const res = http.get(`${BASE_URL}/books`);
        failureRate.add(res.status !== 200);
    }
}
