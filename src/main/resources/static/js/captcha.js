function onloadTurnstileCallback() {
  turnstile.render('#create-post-turnstile-container', {
    sitekey: consts.captchaTrunstileSiteKey,
    theme: consts.currentTheme === 'dark' ? 'dark' : 'light',
    language: consts.currentLang === 'ru' ? 'ru' : 'en',
    execution: 'execute',
    appearance: 'interaction-only',
    callback: function(token) {
        document.getElementById("create-post-form").submit();
    },
//    'error-callback': function() {
//        console.error('Turnstile encountered an error.');
//    },
//    'expired-callback': function() {
//        console.warn('Turnstile challenge expired. User needs to re-verify.');
//    },
//    'timeout-callback': function() {
//        console.log('Challenge timed out');
//    }
  });

  window.renderCommentWidget = function(postId, form) {
    const containerId = '#create-comment-turnstile-container-' + postId;

    turnstile.render(containerId, {
      sitekey: consts.captchaTrunstileSiteKey,
      theme: consts.currentTheme === 'dark' ? 'dark' : 'light',
      language: consts.currentLang === 'ru' ? 'ru' : 'en',
      execution: 'execute',
      appearance: 'interaction-only',
      callback: function(token) {
          commentPost(postId,form,event,token);
      },
//      'error-callback': function() {
//          console.error('Turnstile encountered an error.');
//      },
//      'expired-callback': function() {
//          console.warn('Turnstile challenge expired. User needs to re-verify.');
//      },
//      'timeout-callback': function() {
//          console.log('Challenge timed out');
//      }
     });
  };

  window.turnstileExecute = function(widgetId) {
    turnstile.execute(widgetId);
    return false;
  };

  const containers = document.querySelectorAll('[id^="create-comment-turnstile-container-"]');
  containers.forEach(container => {
    if (container.dataset.turnstileRendered) return;
    const postId = container.id.replace('create-comment-turnstile-container-', '');
    renderCommentWidget(postId,container.closest('form'));
    container.dataset.turnstileRendered = 'true';
  });
}

