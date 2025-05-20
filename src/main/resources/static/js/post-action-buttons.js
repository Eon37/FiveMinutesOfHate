function likePost(postId) {
  fetch(`/api/posts/${postId}/like-async`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' }
  })
    .then(res => res.json())
    .then(data => {
      document.getElementById(`like-post-count-${postId}`).textContent = data;
    })
    .catch(err => console.error(err));
}

function likeComment(postId, commentId) {
  fetch(`/api/posts/${postId}/comments/${commentId}/like-async`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' }
  })
    .then(res => res.json())
    .then(data => {
      document.getElementById(`like-comment-count-${postId}-${commentId}`).textContent = data;
    })
    .catch(err => console.error(err));
}

function commentPost(postId, form, event) {
  event.preventDefault();

  const commentText = form["post-comment-input"].value;
  if (!commentText.trim()) return;

  fetch(`/api/posts/${postId}/comments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ comment: commentText })
  })
    .then(response => response.json())
    .then(data => {
      const commentsList = document.getElementById(`comments-list-${postId}`);

      const newComment = document.createElement('li');
      newComment.className = 'posts-comment';
      newComment.innerHTML = `
              <div class="posts-user">${data.comment.author}</div>
              <div class="posts-content">${data.comment.text}</div>
              <div class="comment-actions">
                <button class="posts-button" id="like-comment-${postId}-${data.comment.id}" onclick="likeComment(${postId}, ${data.comment.id})">
                  ☠️ <span class="posts-button-text" id="like-comment-count-${postId}-${data.comment.id}">${data.comment.likes}</span>
                </button>
              </div>
            `;

      commentsList.prepend(newComment);

      // Clear input field
      form["post-comment-input"].value = '';
    })
    .catch(err => console.error(err));
}