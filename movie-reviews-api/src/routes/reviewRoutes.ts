import { Router } from 'express';
import { createReview, getReviews, getReviewCounts } from '../controllers/reviewController';
import { authenticateUser } from '../middleware/authMiddleware';

const router = Router();
router.post('/', authenticateUser, createReview);
router.get('/', getReviews);
router.post('/_counts', getReviewCounts);

export default router;