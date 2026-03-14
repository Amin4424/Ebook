                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (abs(dragOffset) > 100) {
                                        if (dragOffset > 0) viewModel.nextPage() else viewModel.previousPage()
                                    }
                                    dragOffset = 0f
                                },
                                onHorizontalDrag = { _, dragAmount -> dragOffset += dragAmount }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, zoom, _ ->
                                if (zoom > 1.05f) viewModel.increaseFontSize()
                                else if (zoom < 0.95f) viewModel.decreaseFontSize()
                            }
                        }
