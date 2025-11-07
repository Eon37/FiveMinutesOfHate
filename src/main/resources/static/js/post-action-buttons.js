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

function commentPost(postId, form, event, token) {
  event.preventDefault();

  const commentText = form["post-comment-input"].value;
  if (!commentText.trim()) return;
        console.log("Inside function before fetch");

  fetch(`/api/posts/${postId}/comments?token=token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ comment: commentText })
  })
    .then(response => response.json())
    .then(data => {
      const commentsList = document.getElementById(`comments-list-${postId}`);

      const newComment = document.createElement('li');
      newComment.className = 'posts-comment';

      const userDiv = document.createElement('div');
      userDiv.className = 'posts-user';
      userDiv.textContent = data.comment.author;

      const contentDiv = document.createElement('div');
      contentDiv.className = 'posts-content';
      contentDiv.textContent = data.comment.text;

      const actionsDiv = document.createElement('div');
      actionsDiv.className = 'comment-actions';
      actionsDiv.innerHTML = `
            <button class="posts-button" id="like-comment-${postId}-${data.comment.id}" onclick="likeComment(${postId}, ${data.comment.id})">
              ☠️ <span class="posts-button-text" id="like-comment-count-${postId}-${data.comment.id}">${data.comment.likes}</span>
            </button>
      `;

      newComment.appendChild(userDiv);
      newComment.appendChild(contentDiv);
      newComment.appendChild(actionsDiv);

      commentsList.prepend(newComment);

      // Clear input field
      form["post-comment-input"].value = '';
    })
    .catch(err => console.error(err));
}