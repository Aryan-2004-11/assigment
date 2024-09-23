const errorHandler = (err, req, res, next) => {
    console.error(err.message); 

    if (err.response && err.response.status) {
      return res.status(err.response.status).json({
            message: err.response.data.message || 'An error occurred while fetching data.',
            status: err.response.status,
        });
    }
    res.status(500).json({
        message: err.message || 'Internal Server Error',
    });
};
module.exports = errorHandler;
