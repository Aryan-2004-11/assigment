const express = require('express');
const axios = require('axios');
const router = express.Router();
// Task 1: Get products by category
router.get('/:category', async (req, res, next) => {
    const { category } = req.params;
    try {
        const response = await axios.get(`https://fakestoreapi.com/products/category/${category}`);
        res.json(response.data);
    } catch (error) {
        next(error);
    }
});

// Task 2: Add a new product
router.post('/', async (req, res, next) => {
    const newProduct = req.body;
    try {
        const response = await axios.post('https://fakestoreapi.com/products', newProduct);
        res.status(201).json(response.data);
    } catch (error) {
        next(error);  // Pass error to middleware
    }
});
module.exports = router;
