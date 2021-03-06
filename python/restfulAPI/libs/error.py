class OperationError(Exception):

    def __init__(self, ErrorInfo):
        super().__init__(self)
        self.error_info = ErrorInfo

    @property
    def __str__(self):
        return self.error_info