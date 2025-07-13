- Админский API:
1) получение комментария по id:
   GET   /admin/events/{event-id}/comments/{comment-id}
2) изменение комментария:
   PATCH   /admin/events/{event-id}/comments/{comment-id}
3) удаление комментария:
   DELETE   /admin/events/{event-id}/comments/{comment-id}

- Приватный API:
1) получение всех комментариев пользователя к событию:
   GET   /users/{user-id}/events/{event-id}/comments?from=0&size=10
2) создание комментария:
   POST   /users/{user-id}/events/{event-id}/comments

- Публичный API:
1) получение всех комментариев к событию:
   GET   /events/{event-id}/comments?from=0&size=10